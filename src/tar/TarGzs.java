package tar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class TarGzs {

	public static void main(String[] args) throws Exception {
		// File src = Files.findFile("~/tmp/zzh/abc");
		// File dest = Files.createFileIfNoExists("~/tmp/zzh/abc.tar.gz");
		// targz(src, dest);

		File src = Files.findFile("~/tmp/zzh/abc.tar.gz");
		File dest = Files.createDirIfNoExists("~/tmp/zzh/");
		ugzip(src, dest);
	}

	/**
	 * 将一个 tar.gz 解开成为一个文件夹
	 * 
	 * @param gzipFile
	 *            压缩文件
	 * @param dir
	 *            目标文件夹
	 * 
	 * @return 解开的文件数量
	 */
	public static int ugzip(File gzipFile, final File dir) {
		// 检查源
		if (null == gzipFile || !gzipFile.exists() || !gzipFile.isFile())
			throw Lang.makeThrow("Fail to find src file '%s'", gzipFile);
		if (null == dir)
			throw Lang.makeThrow("null dest");
		if (!dir.isDirectory())
			throw Lang.makeThrow("dest '%s' should be a directory", dir);
		// 检查目标
		if (!dir.exists())
			Files.makeDir(dir);

		return visitTarGz(Streams.fileIn(gzipFile), new InputStreamVisitor() {
			public void visit(String name, InputStream ins) {
				File dest = Files.getFile(dir, name);
				if (!dest.exists())
					try {
						Files.createNewFile(dest);
					}
					catch (IOException e) {
						throw Lang.wrapThrow(e);
					}
				OutputStream ops = Streams.buff(Streams.fileOut(dest));
				try {
					Streams.write(ops, ins);
				}
				catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
				finally {
					Streams.safeClose(ops);
				}

			}
		});
	}

	/**
	 * 遍历一个 tar.gz 的内容
	 * 
	 * @param ins
	 *            压缩文件输入流
	 * @param visitor
	 *            访问器
	 * @return 访问的实体数量
	 */
	public static int visitTarGz(InputStream ins, InputStreamVisitor visitor) {
		// 开始解压缩
		int re = 0;
		TarArchiveInputStream tari = null;
		try {
			// 包裹流
			if (ins instanceof TarArchiveInputStream)
				tari = (TarArchiveInputStream) ins;
			else if (ins instanceof GZIPInputStream)
				tari = new TarArchiveInputStream(ins);
			else
				tari = new TarArchiveInputStream(new GZIPInputStream(Streams.buff(ins)));

			// 循环
			ArchiveEntry te;
			while (null != (te = tari.getNextEntry())) {
				visitor.visit(te.getName(), tari);
			}
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		finally {
			Streams.safeClose(ins);
		}

		// 返回
		return re;
	}

	/**
	 * 将一个源目录压缩成 tar.gz
	 * <p>
	 * 依赖 apache-common-compress-1.2
	 * 
	 * @param srcDir
	 *            源目录
	 * @param dest
	 *            目标文件
	 * @return 目标文件
	 */
	public static File targz(final File srcDir, File dest) {
		// 检查源
		if (null == srcDir || !srcDir.exists() || !srcDir.isDirectory())
			throw Lang.makeThrow("Fail to find src dir '%s'", srcDir);
		if (null == dest)
			throw Lang.makeThrow("null dest");
		if (!dest.isFile())
			throw Lang.makeThrow("dest '%s' should be a file", dest);
		// 检查目标
		if (!dest.exists())
			try {
				Files.createNewFile(dest);
			}
			catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		// 创建压缩流
		TarArchiveOutputStream out = null;
		try {
			BufferedOutputStream bo = Streams.buff(Streams.fileOut(dest));
			out = new TarArchiveOutputStream(new GZIPOutputStream(bo));

			// 开始迭代文件
			final TarArchiveOutputStream tar = out;
			Disks.visitFile(srcDir, new FileVisitor() {
				public void visit(File file) {
					String name = Disks.getRelativePath(srcDir, file);
					name = srcDir.getName() + "/" + name;
					System.out.println(name);
					InputStream ins = null;
					try {
						ArchiveEntry ae = tar.createArchiveEntry(file, name);
						tar.putArchiveEntry(ae);
						ins = Streams.buff(Streams.fileIn(file));
						Streams.write(tar, ins);
						tar.closeArchiveEntry();
						tar.flush();
					}
					catch (IOException e) {
						throw Lang.wrapThrow(e);
					}
					finally {
						Streams.safeClose(ins);
					}
				}
			}, null);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		finally {
			Streams.safeClose(out);
		}

		return dest;
	}

}

package zty;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;
import org.nutz.mvc.HttpAdaptor;

/**
 * 获取页面动态参数数组的适配器
 * 
 * 使用方法:
 * <pre>
 * @AdaptBy(type=AbcAdaptor.class)
 * @At
 * public String hello(Pet[] pets){
 *     ...
 * </pre>
 * 这个适配器将负责填充 pets
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class AbcAdaptor implements HttpAdaptor {

	private Borning<?> borning;

	private Mirror<?> mirror;

	/**
	 * 适配器初始化，根据当前入口方法，做一些分析
	 */
	@Override
	public void init(Method method) {
		// 第一个参数必定是个数组
		Class<?> firstArgType = method.getParameterTypes()[0];
		// 得到数组的元素类型
		mirror = Mirror.me(firstArgType.getComponentType());
		// 存储生成实例的方式
		borning = mirror.getBorning();
	}

	/**
	 * 每次 HTTP 请求都调用这个方法
	 */
	@Override
	public Object[] adapt(	ServletContext sc,
							HttpServletRequest req,
							HttpServletResponse resp,
							String[] pathArgs) {
		// 这个将是函数调用的参数表
		Object[] args = new Object[1];

		// 分析请求的参数表，记录所有数组类型的参数，这些参数将会被组成对象
		Map<?,?> params = req.getParameterMap();
		List<String> names = new ArrayList<String>(params.size());
		List<Object> vals = new ArrayList<Object>(params.size());
		int len = -1; // 记录一下值的数组长度，以便如果发现不一样长，报错
		for (Object key : params.keySet()) {
			Object val = params.get(key);
			if (null == val)
				continue;
			// 值是 array，记录一下
			if (val.getClass().isArray()) {
				names.add(key.toString());
				vals.add(val);
				if (len < 0)
					len = Array.getLength(val);
				else if (len != Array.getLength(val))
					throw Lang.makeThrow("param '%s' value length different with previous", names);
			}
		}

		// 长度一致，那么让我们安全的生成对象吧 ^_^
		Object objs = Array.newInstance(mirror.getType(), names.size());
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			Object val = vals.get(i);
			Object obj = borning.born(new Object[]{});
			mirror.setValue(obj, name, val);
			Array.set(objs, i, obj);
		}

		// 返回参数表
		return args;
	}

}

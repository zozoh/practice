package zzh;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("zzh_obj")
public class TheObj {
	@Column
	@Id
	public int id;

	@Column
	@Name
	public String name;

	@Column
	public char char_p;

	@Column
	public int x,y;


}

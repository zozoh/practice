package zzh;

import org.nutz.dao.entity.annotation.*;

@Table("t_abc")
public class Abc {

	@Id
	private int id;

	@Name
	private String name;

	private Abc abc;

	public Abc getAbc() {
		return abc;
	}

	public void setAbc(Abc abc) {
		this.abc = abc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

package chindb;

import org.nutz.dao.entity.annotation.*;

@Table("t_chin")
public class Chin {

	@Column("序号")
	@Id
	private int id;

	@Column("名字")
	@Name
	private String name;

	@Column("描述")
	private String description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

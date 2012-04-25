package zzh;

import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.meta.Master;
import org.nutz.dao.test.meta.Pet;
import org.nutz.service.IdEntityService;
import org.nutz.trans.Molecule;

public class AbcService extends IdEntityService<Pet> {

	public AbcService(Dao dao) {
		super(dao);
	}
	
	public void doSomething(){
		Molecule<String> m = new Molecule<String>(){
			public void run(){
				Pet pet = dao().fetch(Pet.class,"abc");
				setObj(pet.getName());
			}
		};
		FieldFilter.create(Pet.class, "x|y").set(Master.class, "name").run(m);
		System.out.print(m.getObj());
	}

}

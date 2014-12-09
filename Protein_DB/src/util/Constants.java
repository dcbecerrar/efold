package util;

public class Constants {
	public enum Dir{

		PARA,ANTI,NONE;

		public Dir opposite() {
			if(this == NONE)
				return NONE;
			
			else if(this == PARA)
				return ANTI;
			else
				return PARA;
		}
	}
	
	public enum Environment {

		BURIED, EXPOSED, LOOP;

		public Environment opposite() {
			if(this == BURIED)
				return EXPOSED;
			else
				return BURIED;
		}
	}
	

}

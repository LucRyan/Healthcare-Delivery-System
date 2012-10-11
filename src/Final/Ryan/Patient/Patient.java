package Final.Ryan.Patient;

public class Patient {

	private int id;
	private int picture;
	private String name;
	private String latitude;
	private String longtitude;

	public int getId() {
		return id;
	}
	
	public int getPicture() {
		return picture;
	}

	public String getName() {
		return name;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public String getLongtitude() {
		return longtitude;
	}

	public Patient(int _id, int _picture, String _name, String _latitude, String _longtitude) {
		id = _id;
		picture = _picture;
		name = _name;
		latitude = _latitude;
		longtitude = _longtitude;
	}

	@Override
	public String toString() {
		return name + ": ";
	}
	
}

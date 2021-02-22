package ibm.cn.application.model;

public class Customer {
	
	private String _id;
	private String _rev;
	private String username;
	private String password;
    private String firstName;
    private String lastName;
	private String email;
	private String imageUrl;
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Customer(String _id, String _rev, String username, String password, String firstName, String lastName,
			String email, String imageUrl) {
		super();
		this._id = _id;
		this._rev = _rev;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.imageUrl = imageUrl;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_rev() {
		return _rev;
	}
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		return "Customer [_id=" + _id + ", _rev=" + _rev + ", username=" + username + ", password=" + password
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", imageUrl=" + imageUrl
				+ "]";
	}

}

package invokevirtualcons;

// UnixPath
public class Bar {
	
	private final Baz baz;
	private final byte[] path;
	
	public Bar(Baz baz, byte[] path) {
		this.baz = baz;
		this.path = path;
	}
	
	public Bar(Baz baz, String dir) {
		this(baz, dir.getBytes());
	}

	private static byte[] resolve(byte[] one, byte[] two) {
		int oneLen = one.length;
		int twoLen = two.length;
		byte[] oneTwo = new byte[oneLen + twoLen];
		System.arraycopy(one, 0, oneTwo, 0, oneLen);
		System.arraycopy(two, 0, oneTwo, oneLen, twoLen);
	    return oneTwo;
	}
	
	// toAbsolutePath
	public Bar toBar() {
		return new Bar(getBaz(), resolve(getBaz().dir(), path));
	}
	
	Baz getBaz() {
		return baz;
	}
}

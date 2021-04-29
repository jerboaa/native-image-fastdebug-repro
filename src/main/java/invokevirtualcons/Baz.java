package invokevirtualcons;

// UnixFileSystem
public class Baz {

	@SuppressWarnings("unused")
	private final Foo foo;
	private final byte[] byteArray;
	
	// package-private
	Baz(Foo foo, String dir) {
		System.out.println("constructor original");
		this.foo = foo;
		this.byteArray = dir.getBytes();
		byte[] copy = new byte[byteArray.length - 3];
		System.arraycopy(byteArray, 0, copy, 0, copy.length);
	}
	
	byte[] dir() {
		return byteArray;
	}
	
	Bar getBar() {
		return new Bar(this, dir());
	}
}

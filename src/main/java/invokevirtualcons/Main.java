package invokevirtualcons;

public class Main {

    public static void main(String[] args) throws Exception {
       Foo foo = new Foo();
       String dir = "dir == " + System.getProperty("user.dir");
       Baz b = new Baz(foo, dir);
       b.getBar().toBar();
       new Bar(b, dir.getBytes());
    }

}

public class Test {
    public static void go() {
        Container c1 = new Container();
        Item i1 = new Item();
        c1.setItem(i1);

        Container c2 = new Container();
        Item i2 = new Item();
        c2.setItem(i2);

        Container c3 = c2;
        c3.setItem(i1);
    }

    public static void main(String[] args) {
        go();
    }
}
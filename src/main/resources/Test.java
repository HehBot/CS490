public class Test {
    private int f0;
    private int f1;

    public Test()
    {
    }

    public void setter_f0(int v)
    {
        f0 = v;
    }

    public void setter_f1(int v)
    {
        f1 = v;
    }

    public void print()
    {
        System.out.print("T{" + String.valueOf(f0) + "," + String.valueOf(f1) + "}");
    }

    public static void main(String[] args)
    {
        Test t = new Test();

        t.setter_f0(1);
        t.setter_f1(2);

        t.print();
        System.out.println();
    }
}

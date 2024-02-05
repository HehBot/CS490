package rnd;

public class MainClass {
    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting...");
        String[] sootArgs = { "-process-dir", "src/main/resources", "-pp", "Test" };
        soot.Main.main(sootArgs);
    }
}
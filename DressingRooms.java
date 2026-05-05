public class DressingRoomSimulation {
    public static void main(String[] args) {
        scenario01();
        scenario02();
        scenario03();
    }

    public static void scenario01() {
        Scenario scenario = new Scenario(2, 10, 0);
        scenario.runScenario("Scenario 01: 2 dressing rooms, 10 customers, random items");
    }

    public static void scenario02() {
        Scenario scenario = new Scenario(3, 10, 0);
        scenario.runScenario("Scenario 02: 3 dressing rooms, 10 customers, random items");
    }

    public static void scenario03() {
        Scenario scenario = new Scenario(4, 10, 0);
        scenario.runScenario("Scenario 03: 4 dressing rooms, 10 customers, random items");
    }
}
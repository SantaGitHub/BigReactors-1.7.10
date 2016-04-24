package erogenousbeef.bigreactors;

public enum ModObject {

    //Machines
    blockLiquidizer;

    public final String unlocalisedName;

    private ModObject() {
        unlocalisedName = name();
    }
}

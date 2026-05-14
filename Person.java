public abstract class Person {

    protected int id;
    protected String name;
    protected int age;

    /** Constructor – initialises common person attributes. */
    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    /** Displays basic person information. */
    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
    }

    /** Abstract method – must be implemented by subclasses. */
    public abstract void evaluateStatus();
}
package nl.omgwtfbbq.delver;

/**
 * Main to test Delver agent with.
 */
public class TestRunner {

    public TestRunner() {
    }

    public void run() {
        BaseClass b = new TheClass();
        b.abstractMethod();
        b.concreteMethodInBaseClass();

        TheClass z = new TheClass();
        z.abstractMethod();
        z.concreteMethod();
        b.concreteMethodInBaseClass();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.run();
    }

    class TheClass extends BaseClass {

        public void concreteMethod() {
        }

        @Override
        protected void abstractMethod() {
        }
    }

    abstract class BaseClass {

        public void concreteMethodInBaseClass() {
        }

        protected abstract void abstractMethod();
    }
}

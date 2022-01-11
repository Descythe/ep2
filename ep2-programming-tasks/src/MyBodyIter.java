import java.util.NoSuchElementException;

public class MyBodyIter implements BodyIterator {
    private MyCosmicComponentNode node;
    private MyBodyIter next;
    private MyCosmicComponentNode toRemove = null;

    public MyBodyIter(MyCosmicComponentNode node, MyBodyIter next) {
        this.node = node;
        this.next = next;
    }

    @Override
    public boolean hasNext() {
        return node != null;
    }

    @Override
    public Body next() {
        if (hasNext()) {

            CosmicComponent c = node.getComponent();

            assert(node != null && node.getComponent().getClass() == Body.class);

            toRemove = node;

            Body b = (Body)c;

            do {
                next = node.nextStep(next);
                node = next.node;
                next = next.next;
            } while (node != null && node.getComponent().getClass() != Body.class);

            return b;
        }
        throw new NoSuchElementException("This iteration has no bodies!");
    }

    public void remove() {
        if (toRemove == null) {
            throw new IllegalStateException();
        }
        toRemove.remove();
        toRemove = null;
    }

}

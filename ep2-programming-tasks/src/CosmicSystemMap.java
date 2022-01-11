import java.util.NoSuchElementException;

import static java.lang.Math.abs;

public class CosmicSystemMap implements CosmicSystemIndex {

    private MyKeyValuePair[] table;

    // Creates a hash map from the specified 'system'.
    // The resulting map has multiple (key, value) pairs, one for each
    // body of 'system'. The value is the reference
    // to the system (only the direct parent) to which the body belongs.
    public CosmicSystemMap(ComplexCosmicSystem system) {

        if (system != null) {
            table = new MyKeyValuePair[system.numberOfBodies() * 3];
            add(system);
        }
    }

    private void add(ComplexCosmicSystem system) {
        BodyIterator iter = system.iterator();
        Body body;
        MyKeyValuePair pair;

        while (iter.hasNext()) {
            body = iter.next();
            pair = new MyKeyValuePair(body, system.getParent(body));

            if (!contains(body)) {
                addToTable(pair);
            }
        }
    }

    private void addToTable(MyKeyValuePair pair) {
        int key_hash = pair.key().hashCode();

        table[getFreeIndex(key_hash)] = pair;
    }

    private int getFreeIndex(int hash) {
        int pair_index = abs(hash) % table.length;

        while (table[pair_index] != null) {
            pair_index = (pair_index + 1) % table.length;
        }
        return pair_index;
    }

    @Override
    public ComplexCosmicSystem getParent(Body b) {
        if (b == null || table == null) return null;

        int body_hash = b.hashCode();
        int pair_index = abs(body_hash) % table.length;
        int free_index = getFreeIndex(body_hash);

        while (pair_index != free_index) {
            // table[pair_index] != null
            if (b.equals(table[pair_index].key())) {
                return table[pair_index].value();
            }
            pair_index++;
        }

        return null;
    }

    @Override
    public boolean contains(Body b) {
        if (b == null || table == null) return false;

        int body_hash = b.hashCode();
        int pair_index = abs(body_hash) % table.length;
        int free_index = getFreeIndex(body_hash);

        while (pair_index != free_index) {
            // // table[pair_index] != null
            if (b.equals(table[pair_index].key())) {
                return true;
            }
            pair_index = (pair_index + 1) % table.length;
        }

        return false;
    }

    @Override
    public String toString() {
        String s = "";

        for (MyKeyValuePair myKeyValuePair : table) {
            if (myKeyValuePair != null) {
                s += myKeyValuePair.key().toString() + "\n";
            }
        }

        return s;
    }

    public BodyCollection getBodies() {

        BodyCollection list = new BodyCollection () {

            public boolean add(Body b) {
                return false;
            }
            public boolean contains(Body b) {
                return CosmicSystemMap.this.contains(b);
            }
            public int size() {
                int size = 0;
                for (MyKeyValuePair kv: table) {
                    if (kv != null) {
                        size++;
                    }
                }
                return size;
            }

            public Body[] toArray() {

                Body[] result = new Body[size()];
                int i = 0;
                for (MyKeyValuePair kv: table) {
                    if (kv != null) {
                        result[i++] = kv.key();
                    }
                }
                return result;
            }

            public BodyIterator iterator () {
                return new BodyIterator() {
                    int i = 0;
                    int returned = 0;
                    int size = size();
                    int toRemove = -1;
                    @Override
                    public boolean hasNext() {
                        return returned < size;
                    }

                    @Override
                    public Body next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        while (table[i] == null) {
                            i++;
                        }
                        returned++;
                        toRemove = i;
                        return table[i++].key();
                    }

                    public void remove() {
                        if (toRemove == -1) throw new IllegalStateException();

                        if (toRemove < table.length) {
                            table[toRemove] = null;
                            for (toRemove = (toRemove + 1) & (table.length - 1); table[toRemove] != null;
                                 toRemove = (toRemove + 1) & (table.length - 1)) {
                                MyKeyValuePair kv = table[toRemove];
                                table[toRemove] = null;
                                addToTable(kv);
                            }
                            toRemove = -1;
                        }

                    }
                };
            }
        };

        for (MyKeyValuePair kv: table) {
            if (kv != null) {
                list.add(kv.key());
            }
        }
        return list;
    }
}

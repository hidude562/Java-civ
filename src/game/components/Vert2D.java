package game.components;

class Vert2D {
    private int x;
    private int y;

    public Vert2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vert2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    ;

    public int getY() {
        return y;
    }

    ;

    public static Vert2D delta(Vert2D a, Vert2D b) {
        return new Vert2D(b.getX() - a.getX(), b.getY() - a.getY());
    }

    public static Vert2D add(Vert2D a, Vert2D b) {
        return new Vert2D(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Vert2D abs(Vert2D a) {
        return new Vert2D(Math.abs(a.getX()), Math.abs(a.getY()));
    }

    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public boolean equals(Vert2D o) {
        return o.getX() == x && o.getY() == y;
    }
}

interface Observer {
    void update(Notification notification);
}

interface Subject {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Grade grade);
}

public class Notification {

    private String msj;

    public Notification(String msj) {
        this.msj = msj;
    }

    public String toString() {
        return msj;
    }
}
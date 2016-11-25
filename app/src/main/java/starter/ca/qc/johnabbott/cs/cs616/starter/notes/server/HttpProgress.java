package starter.ca.qc.johnabbott.cs.cs616.starter.notes.server;

/**
 * Created by ian on 2016-11-01.
 */
public class HttpProgress {

    public enum Phase { SENDING, RECEIVING }

    private Phase phase;
    private int total;
    private int progress;

    public HttpProgress() {
    }

    public HttpProgress(Phase phase, int total, int progress) {
        this.phase = phase;
        this.total = total;
        this.progress = progress;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "HttpProgress{" +
                "phase=" + phase +
                ", total=" + total +
                ", progress=" + progress +
                '}';
    }
}

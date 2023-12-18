package main;

public class Stats {
    private String name;
    private int remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    /**
     * simple constructor
     */
    public Stats() {

    }

    /**
     * getter for audio file name
     * @return audio file name
     */
    public String getName() {
        return name;
    }

    /**
     * setter for audio file name
     * @param name new audio file name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * getter for remained time in audio file
     * @return remained time in audio file
     */
    public int getRemainedTime() {
        return remainedTime;
    }

    /**
     * setter for remained time in audio file
     * @param remainedTime new remained time value
     */
    public void setRemainedTime(final int remainedTime) {
        this.remainedTime = remainedTime;
    }

    /**
     * getter for repeat status
     * @return repeat status
     */
    public String getRepeat() {
        return repeat;
    }

    /**
     * setter for repeat status
     * @param repeat new repeat status
     */
    public void setRepeat(final String repeat) {
        this.repeat = repeat;
    }

    /**
     * getter for shuffle status
     * @return shuffle status
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * setter for shuffle status
     * @param shuffle new shuffle status
     */
    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * getter for pause status
     * @return pause status
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * setter for paused status
     * @param paused new pause status
     */
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

}

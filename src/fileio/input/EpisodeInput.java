package fileio.input;

import main.Wrappeable;

import java.util.Objects;

public final class EpisodeInput implements Wrappeable {
    private String name;
    private Integer duration;
    private String description;

    public EpisodeInput() {
    }

    /**
     * implemented method for Wrappeable interface
     * @return name of the Wrappeable for later wrapped command
     */
    @Override
    public String extractName() {
        return this.name;
    }

    /**
     * equals method overrode for usage in hashmap;
     * all fields taken into consideration
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeInput that = (EpisodeInput) o;

        if (!name.equals(that.name)) return false;
        if (!duration.equals(that.duration)) return false;
        return Objects.equals(description, that.description);
    }

    /**
     * hashcode method overrode for usage in hashmap;
     * all fields taken into consideration
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

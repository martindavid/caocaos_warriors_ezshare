package EZShare;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ezshare.client.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Resource {
	@JsonView(Views.Common.class)
	public String name;
	@JsonView(Views.Common.class)
	public String description;
	@JsonView(Views.Common.class)
	public String[] tags;
	@JsonView(Views.Common.class)
	public String uri;
	@JsonView(Views.Common.class)
	public String channel;
	@JsonView(Views.Common.class)
	public String owner;
	@JsonView(Views.Common.class)
	public String ezserver;
	@JsonView(Views.FileReceive.class)
	public long resourceSize;

	/**
	 * Resource constructor, set default value for all properties
	 */
	public Resource() {
		this.uri = "";
		this.name = "";
		this.description = "";
		this.owner = "";
		this.channel = "";
		this.tags = new String[0];
	}

	/***
	 * Copy Constructor to deep copy it
	 * 
	 * @param res
	 */
	public Resource(Resource res) {
		this.name = res.name;
		this.description = res.description;
		this.uri = res.uri;
		this.channel = res.channel;
		this.owner = res.owner;
		this.ezserver = res.ezserver;
		this.tags = res.tags;
	}

	public String toFetchResultJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithView(Views.FileReceive.class).writeValueAsString(this);
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithView(Views.Common.class).writeValueAsString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
				.append(name)
				.append(description)
				.append(uri)
				.append(channel)
				.append(owner)
				.append(tags)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Resource))
            return false;
        if (obj == this)
            return true;
        Resource other = (Resource) obj;
        boolean isEqual = new EqualsBuilder()
        		.append(name, other.name)
        		.append(description, other.description)
        		.append(uri, other.uri)
        		.append(channel, other.channel)
        		.append(owner, other.owner)
        		.append(tags, other.tags)
        		.isEquals();
        return isEqual;
	}
}

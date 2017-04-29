package EZShare;

public class ResourceTemplate {
	public String name;
	public String description;
	public String[] tags;
	public String uri;
	public String channel;
	public String owner;
	public String ezserver;
	
	public ResourceTemplate(Resource res) {
		this.name = res.name;
		this.description = res.description;
		this.uri = res.uri;
		this.channel = res.channel;
		this.owner = res.owner;
		this.ezserver = res.ezserver;
		this.tags = res.tags;
	}
}

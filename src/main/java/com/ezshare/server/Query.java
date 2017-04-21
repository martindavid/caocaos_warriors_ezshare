package com.ezshare.server;

import java.util.ArrayList;
import java.util.Arrays;

import org.pmw.tinylog.Logger;

import com.ezshare.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Query {
	private Resource resource;

	public Query(Resource resource) {
		this.resource = resource;
	}

	private Resource checkOwner(Resource res) {
		if (!Utilities.isEmpty(res.owner)) {
			Resource resCopy = new Resource(res);
			resCopy.owner = "*";
			return resCopy;
		} else {
			return res;
		}
	}

	public ArrayList<Resource> getResourceList() {
		ArrayList<Resource> result = new ArrayList<Resource>();
		Logger.debug("Resource list size: " + Resource.resourceList.size());
		for (Resource res : Resource.resourceList) {
			Resource newRes = new Resource(res);
			if (isMatch(newRes, this.resource)) {
				if (!newRes.owner.isEmpty()) {
					newRes.owner = "*";
				}
				result.add(newRes);
			}
		}

		return result;
	}

	private Boolean isMatch(Resource res, Resource template) {
		Boolean result = false;
		Logger.debug(res.channel);
		Logger.debug(res.owner);
		Logger.debug(res.uri);
		
		if ((res.channel.equals(template.channel)) && 
			(res.name.contains(template.name) || (template.name.isEmpty()))&& 
			(res.description.contains(template.description) || (template.description.isEmpty()))&& 
			(res.uri.contains(template.uri) || (template.uri.isEmpty()))&& 
			(res.owner.contains(template.owner) || (template.owner.isEmpty()))) {
			if (template.tags.length > 0) {
				result = Arrays.asList(res.tags).containsAll(Arrays.asList(template.tags));
			} else {
				result = true;
			}

		}

		return result;
	}

	public ArrayList<Resource> processQuery() throws JsonProcessingException {
		Resource res = this.resource;
		ArrayList<Resource> resultList = new ArrayList<Resource>();

		// TODO Check for missing template

		for (Resource resourceIterator : Resource.resourceList) {
			if (res.channel.equals(resourceIterator.channel)) {
				if (res.owner.equals(resourceIterator.owner)) {
					if (res.uri.equals(resourceIterator.uri)) {
						if (resourceIterator.name.contains(res.name)) {
							if (resourceIterator.name.contains(resourceIterator.name)
									|| resourceIterator.description.contains(resourceIterator.description)
									|| (Utilities.isEmpty(res.name) && Utilities.isEmpty(res.description))) {

								resultList.add(checkOwner(resourceIterator));
							} else {
								resultList.add(checkOwner(resourceIterator));
							}
						}

					} else if (Utilities.isEmpty(res.uri)) {
						if (resourceIterator.name.contains(res.name)) {
							if (resourceIterator.name.contains(resourceIterator.name)
									|| resourceIterator.description.contains(resourceIterator.description)
									|| (Utilities.isEmpty(res.name) && Utilities.isEmpty(res.description))) {
								resultList.add(checkOwner(resourceIterator));
							} else {
								resultList.add(checkOwner(resourceIterator));
							}
						}
					}

				} else if (Utilities.isEmpty(res.owner)) {
					if (res.uri.equals(resourceIterator.uri)) {
						if (resourceIterator.name.contains(res.name)) {
							if (resourceIterator.name.contains(resourceIterator.name)
									|| resourceIterator.description.contains(resourceIterator.description)
									|| (Utilities.isEmpty(res.name) && Utilities.isEmpty(res.description))) {
								resultList.add(checkOwner(resourceIterator));
							} else {
								resultList.add(checkOwner(resourceIterator));
							}
						}

					} else if (Utilities.isEmpty(res.uri)) {
						if (resourceIterator.name.contains(res.name)) {
							if (resourceIterator.name.contains(resourceIterator.name)
									|| resourceIterator.description.contains(resourceIterator.description)
									|| (Utilities.isEmpty(res.name) && Utilities.isEmpty(res.description))) {
								resultList.add(checkOwner(resourceIterator));
							} else {
								resultList.add(checkOwner(resourceIterator));
							}
						}
					}
				}
			}

		}

		return resultList;
	}
}

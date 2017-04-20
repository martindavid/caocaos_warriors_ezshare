package com.ezshare.server;

import java.util.ArrayList;
import java.util.Arrays;

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

		for (Resource res : Resource.resourceList) {
			if (isMatch(res, this.resource)) {
				result.add(res);
			}
		}

		return result;
	}

	private Boolean isMatch(Resource res, Resource template) {
		Boolean result = false;

		if (res.channel.equals(template.channel) && res.owner.equals(template.owner) && res.uri.equals(template.uri)
				&& ((!res.name.isEmpty() && res.name.contains(template.name))
						|| (!res.description.isEmpty() && res.description.contains(template.description))
						|| (template.name.isEmpty() && template.description.isEmpty()))) {
			if (template.tags.length > 0 && Arrays.asList(res.tags).contains(template.tags)) {
				result = true;
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
		// if (resultList.size() <= 0) {
		// QueryResponse resp = new QueryResponse(Utilities.messageReturn(7));
		// return resp;
		// } else {
		// // TODO RETURN LIST
		// // return Utilities.messageReturn(1);
		// QueryResponse resp = new QueryResponse(Utilities.messageReturn(1),
		// resultList);
		// return resp;
		//
		// }

	}
}

package net.earthcomputer.vimapi.util;

import java.util.List;

import com.google.common.collect.Lists;

public class RelativePosition {

	private List<String> before = Lists.newArrayList();
	private List<String> after = Lists.newArrayList();

	private RelativePosition() {
	}

	public static RelativePosition create() {
		return new RelativePosition();
	}

	public RelativePosition before(String name) {
		before.add(name);
		return this;
	}

	public RelativePosition after(String name) {
		after.add(name);
		return this;
	}

	public List<String> getBefore() {
		return before;
	}

	public List<String> getAfter() {
		return after;
	}

}

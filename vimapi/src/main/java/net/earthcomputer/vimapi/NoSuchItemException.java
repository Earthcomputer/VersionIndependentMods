package net.earthcomputer.vimapi;

/**
 * Thrown when an item cannot be resolved from a name
 */
public class NoSuchItemException extends RuntimeException {

	private static final long serialVersionUID = 8617086372977475766L;

	public NoSuchItemException(String name) {
		super(name);
	}

	public String getItemName() {
		return getMessage();
	}

}

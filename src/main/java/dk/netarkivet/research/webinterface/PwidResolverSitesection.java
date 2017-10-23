package dk.netarkivet.research.webinterface;

import dk.netarkivet.common.webinterface.SiteSection;

/**
 * Site section that creates the menu for WPID resolution.
 */
public class PwidResolverSitesection extends SiteSection {
	/**
	 * The number of pages visible in the menu. The visible pages:
	 */
	private static final int PAGES_VISIBLE_IN_MENU = 1;
	private static final String TRANSLATIONS_BUNDLE = "wpid";

	/**
	 * Create a new WpidResolverSitesection SiteSection object.
	 */
	public PwidResolverSitesection() {
		super("mainname;pwidresolver", "PwidResolver", PAGES_VISIBLE_IN_MENU, new String[][] {
				{"PwpidResolver", "pagetitle;pwidresolver"},
		}, 
		"PwidResolver", TRANSLATIONS_BUNDLE);
	}

	/**
	 */
	public void initialize() {
	}

	/** No cleanup necessary in this site section. */
	public void close() {
	}

	public static void main(String[] args) {
	}

}

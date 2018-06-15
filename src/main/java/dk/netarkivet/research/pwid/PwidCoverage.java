package dk.netarkivet.research.pwid;

/**
 * The different types of coverage supported by the 
 * PWID standard.
 * @author svc
 *
 */
public enum PwidCoverage{
    part,
    page,
    subsite,
    site,
    collection,
    snapshot,
    recording,
    other;

    public static String getDescription(PwidCoverage coverage) {
        if (coverage.equals(PwidCoverage.part)) {
            return "the single archived element, e.g. a pdf, a html text, an image";
        } else if (coverage.equals(PwidCoverage.page)) {
            return "the full context as a page, e.g. a html page with referred images";
        } else if (coverage.equals(PwidCoverage.subsite)) {
            return "the full context as a subsite within its domain, e.g. a document"
                    + " represented in a web structure";
        } else if (coverage.equals(PwidCoverage.site)) {
            return "the full context as a site within its domain";
        } else if (coverage.equals(PwidCoverage.collection)) {
            return " a collection/corpora definition";
        } else if (coverage.equals(PwidCoverage.snapshot)) {
            return "a snapshot (image) representation of web material, e.g. a web page";
        } else if (coverage.equals(PwidCoverage.recording)) {
            return "a recording of a web browsing";
        } else if (coverage.equals(PwidCoverage.other)) {
            return "something else";
        } else {
            return "";
        }
    }

}


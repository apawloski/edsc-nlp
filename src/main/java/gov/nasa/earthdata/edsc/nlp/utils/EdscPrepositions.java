package gov.nasa.earthdata.edsc.nlp.utils;

public class EdscPrepositions {

    public enum Prepositions {

        IN("in"), AT("at"), ON("on"), BY("by"), NEXT_TO("next to"), BESIDE("beside"),
        UNDER("under"), BELOW("below"), OVER("over"), ABOVE("above"), ACROSS("across"),
        THROUGH("through"), TO("to"), FROM("from"), INTO("into"), TOWARDS("towards"),
        TOWARD("toward"), ONTO("onto"), OF("of"), OFF("off"), OUT_OF("out of"), ABOUT("about");

        private final String value;

        private Prepositions(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        private final static Prepositions[] values = Prepositions.values();
    }
}

package com.pbt.raadrivers.Pojo;

import java.util.List;

public class GooglePlaces {

    private String status;
    private List<PredictionsBean> predictions;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PredictionsBean> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<PredictionsBean> predictions) {
        this.predictions = predictions;
    }

    public static class PredictionsBean {

        private String description;
        private String id;
        private String place_id;
        private String reference;
        private StructuredFormattingBean structured_formatting;
        private List<MatchedSubstringsBean> matched_substrings;
        private List<TermsBean> terms;
        private List<String> types;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public StructuredFormattingBean getStructured_formatting() {
            return structured_formatting;
        }

        public void setStructured_formatting(StructuredFormattingBean structured_formatting) {
            this.structured_formatting = structured_formatting;
        }

        public List<MatchedSubstringsBean> getMatched_substrings() {
            return matched_substrings;
        }

        public void setMatched_substrings(List<MatchedSubstringsBean> matched_substrings) {
            this.matched_substrings = matched_substrings;
        }

        public List<TermsBean> getTerms() {
            return terms;
        }

        public void setTerms(List<TermsBean> terms) {
            this.terms = terms;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public static class StructuredFormattingBean {

            private String main_text;
            private String secondary_text;
            private List<MainTextMatchedSubstringsBean> main_text_matched_substrings;

            public String getMain_text() {
                return main_text;
            }

            public void setMain_text(String main_text) {
                this.main_text = main_text;
            }

            public String getSecondary_text() {
                return secondary_text;
            }

            public void setSecondary_text(String secondary_text) {
                this.secondary_text = secondary_text;
            }

            public List<MainTextMatchedSubstringsBean> getMain_text_matched_substrings() {
                return main_text_matched_substrings;
            }

            public void setMain_text_matched_substrings(List<MainTextMatchedSubstringsBean> main_text_matched_substrings) {
                this.main_text_matched_substrings = main_text_matched_substrings;
            }

            public static class MainTextMatchedSubstringsBean {

                private int length;
                private int offset;

                public int getLength() {
                    return length;
                }

                public void setLength(int length) {
                    this.length = length;
                }

                public int getOffset() {
                    return offset;
                }

                public void setOffset(int offset) {
                    this.offset = offset;
                }
            }
        }

        public static class MatchedSubstringsBean {

            private int length;
            private int offset;

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }
        }

        public static class TermsBean {

            private int offset;
            private String value;

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}

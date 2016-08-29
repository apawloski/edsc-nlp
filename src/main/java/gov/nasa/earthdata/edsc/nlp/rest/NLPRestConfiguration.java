package gov.nasa.earthdata.edsc.nlp.rest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class NLPRestConfiguration extends Configuration implements AssetsBundleConfiguration {

    @NotEmpty
    @JsonProperty
    private String luceneDir;

    @NotNull
    @JsonProperty
    private Integer maxHitDepth;

    @NotNull
    @JsonProperty
    private Integer maxContextWindow;

    @NotNull
    @JsonProperty
    private Boolean fuzzy;
    
    @NotNull
    @JsonProperty
    private Boolean suTimeIncludeRange;
    
    @NotNull
    @JsonProperty
    private Boolean suTimeMarkTimeRanges;

    @NotNull
    @JsonProperty
    private String geoNamesUrl;
    
    @NotNull
    @JsonProperty
    private String geoNamesUserId;
    
    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    public String getLuceneDir() {
        return luceneDir;
    }

    public Integer getMaxHitDepth() {
        return maxHitDepth;
    }

    public Boolean getFuzzy() {
        return fuzzy;
    }

    public Integer getMaxContextWindow() {
        return maxContextWindow;
    }
    
    public Boolean getSuTimeIncludeRange() {
        return suTimeIncludeRange;
    }
    
    public Boolean getSuTimeMarkTimeRanges() {
        return suTimeMarkTimeRanges;
    }
    
    public String getGeoNamesUrl() {
        return geoNamesUrl;
    }
    
    public String getGeoNamesUserId() {
        return geoNamesUserId;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

}

package org.hilel14.archie.enhabsor.core.jobs.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author hilel
 */
public class ArchieItem extends HashMap<String, Object> {

    private String id;

    public static ArchieItem fromMap(Map<String, String> map) {
        ArchieItem item = new ArchieItem();
        for (String key : map.keySet()) {
            switch (key) {
                case "id":
                    item.setId(map.get(key));
                    break;
                case "dcCreator":
                    item.put(key, map.get(key).split(","));
                    break;
                case "localStoragePermanent":
                    item.put(key, Boolean.parseBoolean(map.get(key)));
                    break;
                default:
                    item.put(key, map.get(key));
            }
        }
        return item;
    }

    public SolrInputDocument toSolrCreate() {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        keySet().forEach(key -> {
            doc.addField(key, get(key));
        });
        return doc;
    }

    public SolrInputDocument toSolrUpdate() {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        keySet().forEach(key -> {
            Map<String, Object> val = new HashMap<>();
            val.put("set", get(key));
            doc.addField(key, val);
        });
        return doc;
    }

    public String getOriginalFileName() {
        if (get("dcFormat") == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        b.append(id).append(".").append(get("dcFormat"));
        return b.toString();
    }

    public String getThumbnailFileName() {
        if (get("dcFormat") == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        b.append(id).append(".").append("png");
        return b.toString();
    }

    public String getTextFileName() {
        if (get("dcFormat") == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        b.append(id).append(".").append("txt");
        return b.toString();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}

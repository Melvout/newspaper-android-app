package es.upm.etsiinf.news_manager.model;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.simple.JSONObject;

public abstract class ModelEntity {
    protected int id;

    public ModelEntity(){

    }

    /**
     *
     * @return the object id (-1) if not saved
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return hashtable of attributes of the entity (without id)
     */
    protected abstract Hashtable<String,String> getAttributes();

    /**
     *
     * @return json object of the entity
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON(){
        JSONObject jsonArticle = new JSONObject();
        if(getId()>0)
            jsonArticle.put("id", getId());

        Hashtable<String,String> res = getAttributes();
        Enumeration<String> keys = res.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            jsonArticle.put(key, JSONObject.escape(res.get((key))));
        }
        return jsonArticle;
    }
}

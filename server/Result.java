
package server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("KVPairs")
    @Expose
    private List<KVPair> kVPairs = null;

    public List<KVPair> getKVPairs() {
        return kVPairs;
    }

    public void setKVPairs(List<KVPair> kVPairs) {
        this.kVPairs = kVPairs;
    }
}
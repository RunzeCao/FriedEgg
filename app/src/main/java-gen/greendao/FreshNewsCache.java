package greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FRESH_NEWS_CACHE".
 */
public class FreshNewsCache {

    private Long id;
    private String result;
    private Integer page;
    private Long time;

    public FreshNewsCache() {
    }

    public FreshNewsCache(Long id) {
        this.id = id;
    }

    public FreshNewsCache(Long id, String result, Integer page, Long time) {
        this.id = id;
        this.result = result;
        this.page = page;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}

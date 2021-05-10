package models;

import java.util.Date;

public class RecordCard
{
    private String publishDate;
    private String recordUrl;
    private String creatorName;
    private float[] effect;

    public RecordCard(){}

    public RecordCard(String publishDate, String recordUrl, String creatorName, float[] effc)
    {
        this.effect = effc;
        this.publishDate = publishDate;
        this.recordUrl = recordUrl;
        this.creatorName = creatorName;
    }

    public String getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(String publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getRecordUrl()
    {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl)
    {
        this.recordUrl = recordUrl;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public void setCreatorName(String userName)
    {
        this.creatorName = userName;
    }

    public float[] getEffect() { return effect; }

    public void setEffect(float[] eff) { this.effect = eff; }
}

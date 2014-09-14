package com.t3coode.togg.services;

import com.t3coode.togg.services.dtos.TagDTO;

public interface TogglTags {

    /**
     * POST /tags creates a tag
     * 
     * @param tagData
     *            TagCreationView containing the creation data
     * 
     *            If the tagData can be parsed to tag, the received data will be
     *            set in this object and the returned tag object will be the
     *            same as tagData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TagDTO object which represents the created Tag
     */
    TagDTO create(TagDTO tagData) throws TogglApiResponseException;

    /**
     * PUT /tags/{id} updates a tag
     * 
     * @param tagData
     *            TagUpdateView which represents the tag data that will be
     *            updated
     * 
     *            If the tagData can be parsed to tag, the received data will be
     *            set in this object and the returned tag object will be the
     *            same as tagData object
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a TagDTO object which represents the Tag
     */
    TagDTO update(TagDTO tagData) throws TogglApiResponseException;

    /**
     * DELETE /tags/{id} deletes a tag
     * 
     * @param tagId
     *            int which represents the tag id
     * 
     * @throws generic
     *             ZukiBoardApiResponseException
     * 
     * @return a boolean which represents whether the tag was removed or not
     */
    boolean delete(int tagId) throws TogglApiResponseException;

}
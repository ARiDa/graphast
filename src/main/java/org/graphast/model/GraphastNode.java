package org.graphast.model;

import it.unimi.dsi.fastutil.BigArrays;

import org.graphast.exception.GraphastException;

import static org.graphast.util.GeoUtils.latLongToInt;

public class GraphastNode {
	public static final short NODE_BLOCKSIZE = 11;

	private Long id;

	private long externalId;

	private int category;

	private int latitude;

	private int longitude;

	private long firstEdge;

	private long labelIndex;

	private long costsIndex;

	private String label;

	public GraphastNode() {

	}

	/**
	 * This method will construct a FastGraphNode with the latitude and longitude passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * @param	latitude	
	 * @param 	longitude
	 */
	public GraphastNode(double latitude, double longitude) {

		this.latitude = latLongToInt(latitude);
		this.longitude = latLongToInt(longitude);
		this.firstEdge = -1;

		if(latitude == Integer.MAX_VALUE || longitude==Integer.MAX_VALUE) {

			throw new GraphastException("Invalid coordinate");

		}

	}

	/**
	 * This method will construct a FastGraphNode with the latitude, longitude
	 * externalId, category, firstEdge, labelIndex and costIndex passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param externalId
	 * @param category
	 * @param latitude
	 * @param longitude
	 * @param firstEdge
	 * @param labelIndex
	 * @param costIndex
	 */
	public GraphastNode(long externalId, int category, double latitude, 
			double longitude, long firstEdge, long labelIndex, long costIndex) {

		this(latitude, longitude);
		this.externalId = externalId;
		this.category = category;
		this.firstEdge = firstEdge;
		this.labelIndex = labelIndex;
		this.costsIndex = costIndex;	

	}

	/**
	 * This method will construct a FastGraphNode with the externalId, latitude and longitude passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param	externalId
	 * @param	latitude	
	 * @param 	longitude
	 */
	public GraphastNode(long externalId, double latitude, double longitude) {

		this(latitude, longitude);
		this.externalId = externalId;

	}

	/**
	 * This method will construct a FastGraphNode with the externalId, latitude, longitude and
	 * label passed.
	 * IMPORTANT: the latitude and longitude will be rounded to six decimal places.
	 * 
	 * @param externalId
	 * @param latitude
	 * @param longitude
	 * @param label
	 */
	public GraphastNode(long externalId, double latitude, double longitude, String label) {

		this(externalId, latitude, longitude);
		this.label = label;

	}

	public void validate(){

		if(latitude == 0 && longitude == 0 && firstEdge == 0){
			throw new GraphastException("Invalid vertex");
		}

	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getExternalIdSegment(){
		return BigArrays.segment(externalId);
	}

	public int getExternalIdOffset(){
		return BigArrays.displacement(externalId);
	}

	public void setExternalId(long externalId) {
		this.externalId = externalId;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getFirstEdgeSegment(){
		return BigArrays.segment(firstEdge);
	}

	public int getFirstEdgeOffset(){
		return BigArrays.displacement(firstEdge);
	}

	public void setFirstEdge(long firstEdge) {
		this.firstEdge = firstEdge;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getLabelIndexSegment(){
		return BigArrays.segment(labelIndex);
	}

	public int getLabelIndexOffset(){
		return BigArrays.displacement(labelIndex);
	}

	public void setLabelIndex(long labelIndex) {
		this.labelIndex = labelIndex;
	}

	public int getCostsIndexSegment(){
		return BigArrays.segment(costsIndex);
	}

	public int getCostsIndexOffset(){
		return BigArrays.displacement(costsIndex);
	}

	public void setCostsIndex(long costIndex) {
		this.costsIndex = costIndex;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "FastGraphNode [id=" + id + ", externalId=" + externalId + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", firstEdge=" + firstEdge + "]";
	}

}
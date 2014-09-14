package com.t3coode.togg.services.dtos;

public class WebSocketMessageDTO extends BaseDTO {

	public static enum ActionTypes {
		UPDATE, INSERT, DELETE, OTHER;
	}

	public static enum ServerModels {
		TIME_ENTRY(TimeEntryDTO.class), USER(UserDTO.class);

		Class<? extends BaseDTO> serverClass;

		private ServerModels(Class<? extends BaseDTO> clazz) {
			this.serverClass = clazz;
		}

		public Class<? extends BaseDTO> getServerClass() {
			return this.serverClass;
		}
	}

	public static final String PING_TYPE = "ping";

	private ActionTypes actionType;
	private String type;
	private BaseDTO data;
	private String rawData;
	private String model;
	private String action;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BaseDTO getData() {
		return data;
	}

	public void setData(BaseDTO data) {
		this.data = data;
	}

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ActionTypes getActionType() {
		return actionType;
	}

	public void setActionType(ActionTypes actionType) {
		this.actionType = actionType;
	}

	public boolean isPing() {
		return type != null && type.equalsIgnoreCase(PING_TYPE);
	}
}

package com.goodlife.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModelProperty;


/**
 * ErrorResponse
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-26T10:47:25.270+05:30")

public class ErrorResponse   {
	@Column(name="message")
	private String message = null;

	public ErrorResponse message(String message) {
		this.message = message;
		return this;
	}

	public ErrorResponse message(List<String> messages) {
		if (messages.size() > 1) {
			this.message = messages.toString();  
		}
		else {
			this.message = messages.get(0);
		}
		return this;
	}

	/**
	 * Get message
	 * @return message
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ErrorResponse errorResponse = (ErrorResponse) o;
		return Objects.equals(this.message, errorResponse.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ErrorResponse {\n");

		sb.append("    message: ").append(toIndentedString(message)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}


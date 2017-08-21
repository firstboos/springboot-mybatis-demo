package com.example.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class City implements Serializable {
	private static final long serialVersionUID = -8968873561791642212L;

	private Long id;

	private String name;

	private String state;

	private String country;
}

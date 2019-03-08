package com.fantasystep.persistence.process

import org.json.JSONObject

trait ProcessTrait {
	def process(name: String, input: JSONObject): JSONObject
}
package com.fantasystep.persistence.process

import org.json.JSONObject

class SimpleProcess extends ProcessTrait {
	def process(name: String, input: JSONObject) = {
	  println("aaa");
	  new JSONObject()
	}
}
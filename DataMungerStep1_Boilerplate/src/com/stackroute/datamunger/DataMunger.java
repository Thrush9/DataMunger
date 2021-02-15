package com.stackroute.datamunger;

/*There are total 5 DataMungertest files:
 * 
 * 1)DataMungerTestTask1.java file is for testing following 3 methods
 * a)getSplitStrings()  b) getFileName()  c) getBaseQuery()
 * 
 * Once you implement the above 3 methods,run DataMungerTestTask1.java
 * 
 * 2)DataMungerTestTask2.java file is for testing following 3 methods
 * a)getFields() b) getConditionsPartQuery() c) getConditions()
 * 
 * Once you implement the above 3 methods,run DataMungerTestTask2.java
 * 
 * 3)DataMungerTestTask3.java file is for testing following 2 methods
 * a)getLogicalOperators() b) getOrderByFields()
 * 
 * Once you implement the above 2 methods,run DataMungerTestTask3.java
 * 
 * 4)DataMungerTestTask4.java file is for testing following 2 methods
 * a)getGroupByFields()  b) getAggregateFunctions()
 * 
 * Once you implement the above 2 methods,run DataMungerTestTask4.java
 * 
 * Once you implement all the methods run DataMungerTest.java.This test case consist of all
 * the test cases together.
 */

public class DataMunger {

	/*
	 * This method will split the query string based on space into an array of words
	 * and display it on console
	 */

	public String[] getSplitStrings(String queryString) {
		 
      String[] splitStrings = queryString.toLowerCase().split(" ");

		return splitStrings;
	}

	/*
	 * Extract the name of the file from the query. File name can be found after a
	 * space after "from" clause. Note: ----- CSV file can contain a field that
	 * contains from as a part of the column name. For eg: from_date,from_hrs etc.
	 * 
	 * Please consider this while extracting the file name in this method.
	 */

	public String getFileName(String queryString) {

		String[] splitStrings = queryString.toLowerCase().split(" ");
		String fileName = " ";
		for (int i = 0; i < splitStrings.length; i++) {
			if (splitStrings[i].contains(".csv")) {
				fileName = splitStrings[i].toString();
			}
		}

		return fileName;
	}

	/*
	 * This method is used to extract the baseQuery from the query string. BaseQuery
	 * contains from the beginning of the query till the where clause
	 * 
	 * Note: ------- 1. The query might not contain where clause but contain order
	 * by or group by clause 2. The query might not contain where, order by or group
	 * by clause 3. The query might not contain where, but can contain both group by
	 * and order by clause
	 */
	
	public String getBaseQuery(String queryString) {

		String[] splitStrings = getSplitStrings(queryString);
		int pos = 0;
		String baseQuery = "";
		if (queryString.contains("where")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("where")) {
					pos = i;
				}
			}
		} else if (!queryString.contains("where") && (queryString.contains("group") || queryString.contains("order"))) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("group") || splitStrings[i].equals("group")) {
					pos = i;
				}
			}
		}
		for (int i = 0; i < pos; i++) {
			baseQuery = baseQuery + splitStrings[i].toString() + " ";
		}

		return baseQuery.trim();
	}

	/*
	 * This method will extract the fields to be selected from the query string. The
	 * query string can have multiple fields separated by comma. The extracted
	 * fields will be stored in a String array which is to be printed in console as
	 * well as to be returned by the method
	 * 
	 * Note: 1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The field
	 * name can contain '*'
	 * 
	 */
	
	public String[] getFields(String queryString) {
		
		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String fieldQuery="";
		
		for (int i = 0; i < splitStrings.length; i++) {
			if (splitStrings[i].equals("select")) {
				start = i;
			} else if (splitStrings[i].equals("from")) {
				stop = i;
			}
		}
		
		for (int i = start+1 ; i < stop; i++) {
			fieldQuery = fieldQuery + splitStrings[i].toString();
		}

		String[] fields = fieldQuery.toLowerCase().split(",");
		return fields;
	}

	/*
	 * This method is used to extract the conditions part from the query string. The
	 * conditions part contains starting from where keyword till the next keyword,
	 * which is either group by or order by clause. In case of absence of both group
	 * by and order by clause, it will contain till the end of the query string.
	 * Note:  1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
	 * might not contain where clause at all.
	 */
	
	public String getConditionsPartQuery(String queryString) {
		
		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String conditionsQuery = "";

		if (queryString.contains("where")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("where")) {
					start = i;
				}
				if (queryString.contains("group") || queryString.contains("order")) {
					if (splitStrings[i].equals("group") || splitStrings[i].equals("order")) {
						stop = i;
					}
				} else
					stop = splitStrings.length;
			}
			for (int i = start + 1; i < stop; i++) {
				conditionsQuery = conditionsQuery + splitStrings[i].toString() + " ";
			}
			conditionsQuery = conditionsQuery.trim();
		}else {
			conditionsQuery = null;
		}
		return conditionsQuery;
	}

	/*
	 * This method will extract condition(s) from the query string. The query can
	 * contain one or multiple conditions. In case of multiple conditions, the
	 * conditions will be separated by AND/OR keywords. for eg: Input: select
	 * city,winner,player_match from ipl.csv where season > 2014 and city
	 * ='Bangalore'
	 * 
	 * This method will return a string array ["season > 2014","city ='bangalore'"]
	 * and print the array
	 * 
	 * Note: ----- 1. The field name or value in the condition can contain keywords
	 * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
	 * might not contain where clause at all.
	 */

	public String[] getConditions(String queryString) {
		
		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String conditionsQuery = "";
		String[] conditions = null;

		if (queryString.contains("where")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("where")) {
					start = i;
				}
				if (queryString.contains("group") || queryString.contains("order")) {
					if (splitStrings[i].equals("group") || splitStrings[i].equals("order")) {
						stop = i;
					}
				} else
					stop = splitStrings.length;
			}
			for (int i = start + 1; i < stop; i++) {
				conditionsQuery = conditionsQuery + splitStrings[i].toString() + " ";
			}

			conditions = conditionsQuery.trim().split(" and | or | not ");
		}

		return conditions;
	}

	/*
	 * This method will extract logical operators(AND/OR) from the query string. The
	 * extracted logical operators will be stored in a String array which will be
	 * returned by the method and the same will be printed Note:  1. AND/OR
	 * keyword will exist in the query only if where conditions exists and it
	 * contains multiple conditions. 2. AND/OR can exist as a substring in the
	 * conditions as well. For eg: name='Alexander',color='Red' etc. Please consider
	 * these as well when extracting the logical operators.
	 * 
	 */

	public String[] getLogicalOperators(String queryString) {

		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String conditionsQuery = "";
		String[] logicalOperators = null;

		if (queryString.contains("where")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("where")) {
					start = i;
				}
				if (queryString.contains("group") || queryString.contains("order")) {
					if (splitStrings[i].equals("group") || splitStrings[i].equals("order")) {
						stop = i;
					}
				} else
					stop = splitStrings.length;
			}
			for (int i = start + 1; i < stop; i++) {
				if (splitStrings[i].equals("and") || splitStrings[i].equals("or") || splitStrings[i].equals("not")) {
					conditionsQuery = conditionsQuery + splitStrings[i].toString() + " ";
				}
			}

			logicalOperators = conditionsQuery.trim().split(" ");
		}

		return logicalOperators;
	}

	/*
	 * This method extracts the order by fields from the query string. Note: 
	 * 1. The query string can contain more than one order by fields. 2. The query
	 * string might not contain order by clause at all. 3. The field names,condition
	 * values might contain "order" as a substring. For eg:order_number,job_order
	 * Consider this while extracting the order by fields
	 */

	public String[] getOrderByFields(String queryString) {

		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String orderByQuery = "";
		String[] orderbyFields = null;

		if (queryString.contains("order")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("order") && splitStrings[i + 1].equals("by")) {
					start = i + 1;
				}

				stop = splitStrings.length;
			}
			for (int i = start + 1; i < stop; i++) {
				orderByQuery = orderByQuery + splitStrings[i].toString() + " ";
			}

			orderbyFields = orderByQuery.trim().split(" ");
		}

		return orderbyFields;
	}

	/*
	 * This method extracts the group by fields from the query string. Note:
	 * 1. The query string can contain more than one group by fields. 2. The query
	 * string might not contain group by clause at all. 3. The field names,condition
	 * values might contain "group" as a substring. For eg: newsgroup_name
	 * 
	 * Consider this while extracting the group by fields
	 */

	public String[] getGroupByFields(String queryString) {

		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String groupByQuery = "";
		String[] groupbyFields = null;

		if (queryString.contains("group")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("group") && splitStrings[i + 1].equals("by")) {
					start = i + 1;
				}

				stop = splitStrings.length;
			}
			for (int i = start + 1; i < stop; i++) {
				groupByQuery = groupByQuery + splitStrings[i].toString() + " ";
			}

			groupbyFields = groupByQuery.trim().split(" ");
		}

		return groupbyFields;
	}

	/*
	 * This method extracts the aggregate functions from the query string. Note:
	 *  1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
	 * followed by "(" 2. The field names might
	 * contain"sum"/"count"/"min"/"max"/"avg" as a substring. For eg:
	 * account_number,consumed_qty,nominee_name
	 * 
	 * Consider this while extracting the aggregate functions
	 */

	public String[] getAggregateFunctions(String queryString) {

		String[] splitStrings = getSplitStrings(queryString);
		int start = 0, stop = 0;
		String aggregateQuery = "", fieldQuery = "";
		String[] aggregates = null, fiels = null;
		if (!queryString.contains("*")) {
			for (int i = 0; i < splitStrings.length; i++) {
				if (splitStrings[i].equals("select")) {
					start = i;
				} else if (splitStrings[i].equals("from")) {
					stop = i;
				}
			}
			for (int i = start + 1; i < stop; i++) {
				fieldQuery = fieldQuery + splitStrings[i].toString();
			}

			String[] fields = fieldQuery.toLowerCase().split(",");

			for (int i = 0; i < fields.length; i++) {
				if (fields[i].contains("sum(") || fields[i].contains("count(") || fields[i].contains("min(")
						|| fields[i].contains("max(") || fields[i].contains("avg(")) {
					aggregateQuery = aggregateQuery + fields[i].toString() + " ";
				}
			}
			aggregates = aggregateQuery.toLowerCase().split(" ");
		}
		return aggregates;
	}
}
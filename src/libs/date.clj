(ns libs.date)

(defn format-date
  ([] (format-date (java.util.Date.) "YYYY-MM-dd hh:mm"))
  ([date] (format-date date "YYYY-MM-dd hh:mm"))
  ([date format] (.format (java.text.SimpleDateFormat. format) date)))
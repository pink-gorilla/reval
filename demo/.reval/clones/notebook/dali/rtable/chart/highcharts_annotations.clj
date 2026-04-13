(ns notebook.dali.rtable.chart.highcharts-annotations
  (:require
   [rtable.plot :as plot]))

(def spec
  {:title {:text "Demo - Annotations"}
   :xAxis {:categories ["Jan" "Feb" "Mar"
                        "Apr" "May" "Jun"
                        "Jul" "Aug" "Sep"
                        "Oct" "Nov" "Dec"]
           :plotBands [{:color "rgba(255,75,66,0.07)"
                        :from 4
                        :to 6
                        :label {:text "forecast"}
                        :zIndex 1000}]}

   :annotations [{:labels [{:point "max" :text "MAX!" :backgroundColor "red"}
                           {:point "min" :text "MIN!" :backgroundColor "white"}
                           {:point {:x 9 :y 150 :xAxis 0 :yAxis 0} :text "label"}]
                  :shapes [{:type "path"
                            :strokeWidth 5
                            :backgroundColor "blue"
                            :fill "blue"
                            :points [{:x 3 :y 105 :xAxis 0 :yAxis 0}
                                     {:x 3 :y 145 :xAxis 0 :yAxis 0}
                                     {:x 5 :y 145 :xAxis 0 :yAxis 0}
                                     {:x 5 :y 105 :xAxis 0 :yAxis 0}]}]}]

   :series [{:data [{:y 29.9 :id "min"}
                    71.5 106.4 129.2 144.0 176.0 135.6 148.5
                    {:y 216.4 :id "max"}
                    194.1 95.6 54.4]}]

   :credits {:enabled false}
   :accessibility {:enabled false}
   :plotOptions {:series {:animation 0}
                 :candlestick {; down
                               :color "red"
                               :lineColor "red"
                                       ; up
                               :upColor "blue"
                               :upLineColor "blue"}}

   :tooltip {:animation {:duration 0}
             :distance 32}

   ;; highstock specific starting here: *************************
      ; The navigator is a small series below the main series, displaying a view of the entire data set.
   :navigator {:enabled false}
   :scrollbar {:enabled false}
    ;The range selector is a tool for selecting ranges to display within 
    ; the chart. It provides buttons to select preconfigured ranges in 
    ; the chart, like 1 day, 1 week, 1 month etc. It also provides input 
    ; boxes where min and max dates can be manually input.
   :rangeSelector {:enabled false}})

(plot/highchart
 {:style {:width "300px"
          :height "300px"}
  :dynamic-height false
  :data spec})


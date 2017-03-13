(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div ul li table tr td th thead tbody]]
   [goog.dom :as gdom]
   [omn1.data :as dat]
   [taoensso.timbre :refer-macros [info]]))

(taoensso.timbre/merge-config! {:level :error :ns-blacklist ["omn1.*"]} )

(defui Car
  static om/Ident
  (ident [this {:keys [id]}]
         [:car/by-id id])
  static om/IQuery
  (query [this] [:db/id :car/make :car/model :year])
  Object
  (render
   [this]
   (let [{id :db/id make :car/make model :car/model year :year} (om/props this)]
     (tr nil
         (td nil make)
         (td nil model)
         (td nil year)
         (td nil id)))))

(def car (om/factory Car {:keyfn :id}))

(defui UserCars
  static om/IQuery
  (query [this] [:user/email :user/age {:user/cars (om/get-query Car)}])
  Object
  (render
   [this]
   (let [{email :user/email age :user/age cars :user/cars} (om/props this)]
     (div nil
          (div nil (str "Current User: " email))
          (div nil (str "Age: " age))
          (div nil "User Cars:")
          (table nil
                 (thead nil
                        (tr nil
                            (th nil "Make")
                            (th nil "Model")
                            (th nil "Year")
                            (th nil "ID")))
                 (tbody nil (map car cars)))))))

(om/add-root! dat/reconciler UserCars (gdom/getElement "app"))

;; ------------ test functions

;; #?(:clj (do (def simple-factory (om/factory UserCars))
;;             (dom/render-to-str (simple-factory))
;;             (om/component? UserCars)
;;             (om/get-query UserCars)))

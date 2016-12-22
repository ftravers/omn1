(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]
   [omn1.data :as dat]))

;; (defui Car
;;   static om/Ident
;;   (ident [this {:keys [id]}]
;;          [:car/by-id id])

;;   static om/IQuery
;;   (query [this] [:make :model :year])

;;   Object
;;   (render
;;    [this]
;;    (let [{:keys [make model year]} (om/props this)]
;;      (dom/div nil
;;               (dom/div nil (str "Make: " make))
;;               (dom/div nil (str "Model: " model))
;;               (dom/div nil (str "Year: " year))))))

;; (query [this] [:current/user {:my-cars (om/get-query Car)}])

(defui MyCars
  static om/IQuery
  (query [this] [:current/user {:my-cars [:id]}])

  Object
  (render
   [this]
   (let [data (om/props this)]
     (div nil (str data)))))

;; (defui MyComponent
;;   Object
;;   (componentDidMount [this]
;;                      (.log js/console "did mount"))
;;   (render [this]
;;           (div nil "Hello, world!")))

(om/add-root! dat/reconciler MyCars (gdom/getElement "app"))

(om/component? MyCars)
(om/get-query MyCars)

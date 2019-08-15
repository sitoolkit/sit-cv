import hash from 'object-hash';

export default class DomUtils {
  public static addEventListenerOnce(
    element: Element,
    type: string,
    listener: EventListenerOrEventListenerObject,
    options?: boolean | AddEventListenerOptions
  ) {
    const key = 'attr_' + hash(listener);    
    if (element.hasAttribute(key)) {
      return;
    }
    element.setAttribute(key, 'true');
    element.addEventListener(type, listener, options);
  }
}

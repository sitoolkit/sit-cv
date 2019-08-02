export default interface MenuItem {
  name?: string;
  endpoint?: string;
  children?: MenuItem[];
}

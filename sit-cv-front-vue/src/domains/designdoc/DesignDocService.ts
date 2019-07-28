import MenuItem from '@/domains/designdoc/MenuItem';

export default interface DesignDocService {
  fetchMenuItems(): Promise<object>;
}

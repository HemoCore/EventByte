import type { Schema, Struct } from '@strapi/strapi';

export interface ValueObjectTicket extends Struct.ComponentSchema {
  collectionName: 'components_value_object_tickets';
  info: {
    displayName: 'Ticket';
    icon: 'file';
  };
  attributes: {
    name: Schema.Attribute.String & Schema.Attribute.Required;
    price: Schema.Attribute.Decimal &
      Schema.Attribute.Required &
      Schema.Attribute.SetMinMax<
        {
          min: 0;
        },
        number
      >;
    quantity: Schema.Attribute.Integer &
      Schema.Attribute.Required &
      Schema.Attribute.SetMinMax<
        {
          min: 0;
        },
        number
      >;
    uuid: Schema.Attribute.String;
  };
}

export interface ValueObjectVenue extends Struct.ComponentSchema {
  collectionName: 'components_value_object_venues';
  info: {
    displayName: 'Venue';
    icon: 'pinMap';
  };
  attributes: {
    adress: Schema.Attribute.String & Schema.Attribute.Required;
    coordinate: Schema.Attribute.JSON &
      Schema.Attribute.CustomField<
        'plugin::geodata.geojson',
        {
          info: false;
        }
      >;
    name: Schema.Attribute.String & Schema.Attribute.Required;
  };
}

declare module '@strapi/strapi' {
  export module Public {
    export interface ComponentSchemas {
      'value-object.ticket': ValueObjectTicket;
      'value-object.venue': ValueObjectVenue;
    }
  }
}
